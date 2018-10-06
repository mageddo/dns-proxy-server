package timed

import (
	"github.com/stretchr/testify/assert"
	"testing"
	"time"
)

func TestTimedValueImpl_IsValid(t *testing.T) {

	current1, err := time.Parse("2006-01-02 15:04:05.999", "2017-07-19 17:00:00.300")
	assert.Nil(t, err)

	current2, err := time.Parse("2006-01-02 15:04:05.999", "2017-07-19 17:00:00.700")
	assert.Nil(t, err)

	expiredTime, err := time.Parse("2006-01-02 15:04:05.999", "2017-07-19 17:00:00.900")
	assert.Nil(t, err)

	assert.True(t, NewTimedValue(1, current1, time.Duration(500 * time.Millisecond)).IsValid(current2))
	assert.False(t, NewTimedValue(1, current1, time.Duration(500 * time.Millisecond)).IsValid(expiredTime))

}
