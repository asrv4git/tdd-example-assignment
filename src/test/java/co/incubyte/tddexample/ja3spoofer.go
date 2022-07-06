package main

// #include <stdlib.h>
import "C"

import (
	"context"
	"encoding/json"
	"fmt"
	"strings"
	"time"
	"unsafe"

	"github.com/Danny-Dasilva/CycleTLS/cycletls"
)

var headers = map[string]string{
	"Accept":                    "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
	"Accept-Encoding":           "gzip, deflate, br",
	"Accept-Language":           "en-GB,en-US;q=0.9,en;q=0.8",
	"Cache-Control":             "max-age=0",
	"Connection":                "keep-alive",
	"Cookie":                    "",
	"DNT":                       "1",
	"Host":                      "hermes.goibibo.com",
	"Sec-Fetch-Dest":            "document",
	"Sec-Fetch-Mode":            "navigate",
	"Sec-Fetch-Site":            "none",
	"Sec-Fetch-User":            "?1",
	"Upgrade-Insecure-Requests": "1",
	"sec-ch-ua":                 "\".Not/A)Brand\";v=\"99\", \"Google Chrome\";v=\"103\", \"Chromium\";v=\"103\"",
	"sec-ch-ua-mobile":          "?0",
	"sec-ch-ua-platform":        "\"macOS\"",
}

var ja3 = "771,4865-4866-4867-49195-49199-49196-49200-52393-52392-49171-49172-156-157-47-53,0-23-65281-10-11-35-16-5-13-18-51-45-43-27-17513,29-23-24,0"
var userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36"
var timeout = 20

//export GetDataForUrls
func GetDataForUrls(commaSeparatedUrls *C.char, proxyPointer *C.char) *C.char {

	urlStrings := C.GoString(commaSeparatedUrls)
	proxy := C.GoString(proxyPointer)

	fmt.Println("urls received: " + urlStrings)

	urls := strings.Split(urlStrings, ",")

	c := make(chan urlResponse)

	var client = cycletls.Init()

	start := time.Now()

	ctx := context.Background()

	for _, url := range urls {
		go asyncSendJas3SpoofCallWithContext(ctx, client, headers, ja3, userAgent, proxy, url, c)
	}

	result := make([]urlResponse, len(urls))

	for i, _ := range result {
		result[i] = <-c
	}

	j, _ := json.MarshalIndent(result, "", "  ")
	json := string(j)
	fmt.Println(json)
	cs := C.CString(json)
	fmt.Printf("Execution time %s\n", time.Since(start))
	return cs
}

//export Free
func Free(str *C.char) {
	C.free(unsafe.Pointer(str))
}

func asyncSendJas3SpoofCallWithContext(ctx context.Context, client cycletls.CycleTLS, headers map[string]string, ja3 string, userAgent string,
	proxy string, url string, c chan urlResponse) {
	ctx, cancel := context.WithTimeout(ctx, time.Duration(50*time.Second))

	go func(ctx context.Context) {

		response, err := client.Do(url, cycletls.Options{
			Body:      "",
			Ja3:       ja3,
			UserAgent: userAgent,
			Headers:   headers,
			Proxy:     proxy,
			Timeout:   timeout,
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
