package parser

import (
	"strings"
)

// Tokenizer performs basic lexical analysis over an input string.
type Tokenizer struct {
	input    string
	position int
}

// NewTokenizer returns a tokenizer for the given input.
func NewTokenizer(input string) *Tokenizer {
	return &Tokenizer{input: input}
}

// Tokenize breaks the input into tokens.
func (t *Tokenizer) Tokenize() []Token {
	var tokens []Token
	for t.position < len(t.input) {
		current := t.input[t.position]

		if isWhitespace(current) {
			t.position++
			continue
		}

		if isDigit(current) {
			tokens = append(tokens, t.readNumber())
			continue
		}

		if isLetter(current) {
			tokens = append(tokens, t.readIdentifier())
			continue
		}

		if strings.ContainsRune("+-*/=(){}", rune(current)) {
			tokens = append(tokens, Token{Type: OPERATOR, Value: string(current)})
			t.position++
			continue
		}

		if current == '"' {
			tokens = append(tokens, t.readString())
			continue
		}

		t.position++
	}

	tokens = append(tokens, Token{Type: EOF, Value: ""})
	return tokens
}

func (t *Tokenizer) readNumber() Token {
	start := t.position
	for t.position < len(t.input) && isDigit(t.input[t.position]) {
		t.position++
	}
	return Token{Type: NUMBER, Value: t.input[start:t.position]}
}

func (t *Tokenizer) readIdentifier() Token {
	start := t.position
	for t.position < len(t.input) && (isLetter(t.input[t.position]) || isDigit(t.input[t.position])) {
		t.position++
	}
	return Token{Type: IDENTIFIER, Value: t.input[start:t.position]}
}

func (t *Tokenizer) readString() Token {
	t.position++ // skip starting quote
	start := t.position
	for t.position < len(t.input) && t.input[t.position] != '"' {
		t.position++
	}
	result := t.input[start:t.position]
	if t.position < len(t.input) {
		t.position++ // skip ending quote
	}
	return Token{Type: STRING, Value: result}
}

func isWhitespace(ch byte) bool { return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r' }
func isDigit(ch byte) bool      { return ch >= '0' && ch <= '9' }
func isLetter(ch byte) bool     { return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') }
