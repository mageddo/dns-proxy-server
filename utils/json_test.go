package utils

import (
	"github.com/stretchr/testify/assert"
	"testing"
)

func TestReplace(t *testing.T) {

	// arrange
	actualJson := `{"id":999}`

	// act
	replacedJson := Replace(`{"id":$1}`, actualJson, `"id":(\d+)`)

	// assert

	assert.Equal(t, replacedJson, actualJson)

}
