package main

import (
	"fmt"
	"strings"
	"runix/go/filemanager"
	"runix/go/processor"
	"runix/go/webserver"
)

func main() {
	fmt.Println("🧪 Test rápido del sistema...")

	// Test response exactly like AI sends it
	testResponse := `Aquí tienes un código HTML simple que muestra "Hello World":
` + "```html" + `
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hello World</title>
</head>
<body>
    <h1>Hello World</h1>
</body>
</html>
` + "```" + `
Puedes copiar este código en un archivo con extensión .html (por ejemplo, "hola_mundo.html") y abrirlo en tu navegador para ver el mensaje "Hello World" en la página.`

	fmt.Println(strings.Repeat("=", 50))
	fmt.Println(testResponse)
	fmt.Println(strings.Repeat("=", 50))
	fmt.Println(strings.Repeat("=", 50))

	// Initialize components
	fm, err := filemanager.NewFileManager()
	if err != nil {
		fmt.Printf("❌ Error: %v\n", err)
		return
	}
	defer fm.Cleanup()

	ws := webserver.NewServer(fm.GetProjectPath())
	proc := processor.NewResponseProcessor(fm, ws)

	// Process the response
	result := proc.ProcessResponse(testResponse)
	fmt.Println("\nResultado:")
	fmt.Println(strings.Repeat("=", 50))
	fmt.Println(result)
	fmt.Println(strings.Repeat("=", 50))
	fmt.Println(strings.Repeat("=", 50))

	// Verify file was created
	fmt.Printf("\nDirectorio de proyecto: %s\n", fm.GetProjectPath())
	
	if ws.CheckIfFileExists() {
		fmt.Println("✅ ¡Archivo index.html creado exitosamente!")
		fmt.Println("🌐 Puedes abrir http://localhost:1111 en tu navegador")
	} else {
		fmt.Println("❌ No se creó el archivo index.html")
	}
}