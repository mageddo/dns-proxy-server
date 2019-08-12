package uuid

import (
"crypto/rand"
"fmt"
	"strings"
)

func UUID() (uuid string) {
	b := make([]byte, 16)
	_, err := rand.Read(b)
	if err != nil {
		fmt.Println("Error: ", err)
		return
	}
	return fmt.Sprintf("%x-%x-%x-%x-%x", b[0:4], b[4:6], b[6:8], b[8:10], b[10:])
}

func TruncatedUUID(size int) string {
	return strings.Replace(UUID(), "-", "", -1)[0:size]
}
