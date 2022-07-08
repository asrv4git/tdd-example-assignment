package main

// #include <stdlib.h>
import "C"

import (
	"context"
	"encoding/json"
	"fmt"
	"strconv"
	"strings"
	"time"
	"unsafe"

	"github.com/Danny-Dasilva/CycleTLS/cycletls"
)

//export GetDataForUrls
func GetDataForUrls(commaSeparatedUrlsPtr *C.char, proxyPtr *C.char, ja3Ptr *C.char, userAgentPtr *C.char,
	httpTimeoutPtr *C.char, headersJsonPtr *C.char, contextCancellationTimeoutPtr *C.char) *C.char {

	urlStrings := C.GoString(commaSeparatedUrlsPtr)
	proxy := C.GoString(proxyPtr)
	ja3 := C.GoString(ja3Ptr)
	userAgent := C.GoString(userAgentPtr)
	httpTimeout, _ := strconv.Atoi(C.GoString(httpTimeoutPtr))
	headersJson := C.GoString(headersJsonPtr)
	contextCancellationTimeout, _ := strconv.Atoi(C.GoString(contextCancellationTimeoutPtr))

	//convert headersJson to headers map
	headers := map[string]string{}
	json.Unmarshal([]byte(headersJson), &headers)

	fmt.Println("Headers: ", headers)
	fmt.Println("contextCancellationTimeout: ", contextCancellationTimeout)
	fmt.Println("httpTimeout: ", httpTimeout)
	fmt.Println("ja3: ", ja3)
	fmt.Println("userAgent: ", userAgent)
	fmt.Println("urls received: " + urlStrings)

	urls := strings.Split(urlStrings, ",")

	//reponse channel
	c := make(chan urlResponse)

	//http client
	var client = cycletls.Init()

	start := time.Now()

	ctx := context.Background()

	for _, url := range urls {
		go asyncSendJas3SpoofCallWithContext(ctx, client, headers, ja3, userAgent, proxy, url, httpTimeout, contextCancellationTimeout, c)
	}

	result := make([]urlResponse, len(urls))

	//extracting result from response channel
	for i, _ := range result {
		result[i] = <-c
	}

	j, _ := json.MarshalIndent(result, "", "  ")
	json := string(j)
	cs := C.CString(json)
	fmt.Printf("Execution time %s\n", time.Since(start))
	return cs
}

//export Free
func Free(str *C.char) {
	C.free(unsafe.Pointer(str))
}

func asyncSendJas3SpoofCallWithContext(ctx context.Context, client cycletls.CycleTLS, headers map[string]string,
	ja3 string, userAgent string, proxy string, url string, httpTimeout int, contextCancellationTimeout int, c chan urlResponse) {
	ctx, cancel := context.WithTimeout(ctx, time.Duration(time.Duration(contextCancellationTimeout)*time.Second))

	go func(ctx context.Context) {

		response, err := client.Do(url, cycletls.Options{
			Body:      "",
			Ja3:       ja3,
			UserAgent: userAgent,
			Headers:   headers,
			Proxy:     proxy,
			Timeout:   httpTimeout,
		}, "GET")

		if err != nil {
			c <- urlResponse{url, response.Status, response.Body, err.Error()}
		} else {
			c <- urlResponse{url, response.Status, response.Body, ""}
		}

		// cancel context by force, assuming the whole process is complete
		cancel()
	}(ctx)

	select {
	case <-ctx.Done():
		switch ctx.Err() {
		case context.DeadlineExceeded:
			c <- urlResponse{url, -1, "GoLang Execution Timeout (Non-network error)", ""}
		}
	}
}

type urlResponse struct {
	Url  string `json:"url"`
	Code int    `json:"code"`
	Body string `json:"body"`
	Err  string `json:"error"`
}

func main() {}
