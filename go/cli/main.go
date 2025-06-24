package main

import (
	"fmt"
	"runix/go/app"
	"runix/go/parser"
	"runix/go/utils"
)

func main() {
	fmt.Println("¡Bienvenido a Runix en Go!")

	app.HelloWorld()

	tokenizer := parser.NewTokenizer("foo = 42")
	tokens := tokenizer.Tokenize()
	utils.Log("Tokens", tokens)
}
