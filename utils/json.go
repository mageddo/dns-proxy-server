package utils

import (
	"fmt"
	"regexp"
	"strings"
)

func Replace(expectedJson string, actualJson string, patterns ...string) string {
	for i, pattern := range patterns {
		regex := regexp.MustCompile(pattern)
		foundMatch := regex.FindStringSubmatch(actualJson)
		if len(foundMatch) == 2 {
			expectedJson = strings.Replace(expectedJson, fmt.Sprintf("$%d", i + 1), foundMatch[1], -1)
		}
	}
	return expectedJson
}
