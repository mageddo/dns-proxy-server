package reference

import (
	"context"
	"github.com/mageddo/dns-proxy-server/pkg/mageddo/uuid"
)

const UUID = "UUID"

func Context() context.Context {
	return context.WithValue(context.Background(), UUID, uuid.TruncatedUUID(10))
}

func getUUID(ctx context.Context) string {
	return ctx.Value(UUID).(string)
}
