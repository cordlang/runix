package parser

type TokenType int

const (
	IDENTIFIER TokenType = iota
	NUMBER
	STRING
	OPERATOR
	EOF
)

type Token struct {
	Type  TokenType
	Value string
}

func (t Token) String() string {
	return "[" + t.Type.String() + ": " + t.Value + "]"
}

func (tt TokenType) String() string {
	switch tt {
	case IDENTIFIER:
		return "IDENTIFIER"
	case NUMBER:
		return "NUMBER"
	case STRING:
		return "STRING"
	case OPERATOR:
		return "OPERATOR"
	case EOF:
		return "EOF"
	default:
		return "UNKNOWN"
	}
}
