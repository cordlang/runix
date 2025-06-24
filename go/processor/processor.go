package processor

import (
	"fmt"
	"regexp"
	"strings"
)

type ResponseProcessor struct {
	fileManager FileManagerInterface
	webServer   WebServerInterface
}

type FileManagerInterface interface {
	ProcessAIResponse(response string) error
	GetProjectPath() string
}

type WebServerInterface interface {
	StartInBackground() chan error
	CheckIfFileExists() bool
}

// NewResponseProcessor creates a new response processor
func NewResponseProcessor(fm FileManagerInterface, ws WebServerInterface) *ResponseProcessor {
	return &ResponseProcessor{
		fileManager: fm,
		webServer:   ws,
	}
}

// ProcessResponse processes the AI response and handles file creation and server deployment
func (rp *ResponseProcessor) ProcessResponse(response string) string {
	fmt.Printf("DEBUG: Procesando respuesta de %d caracteres\n", len(response))
	
	// Check if response contains code blocks
	if rp.containsCodeBlocks(response) {
		fmt.Println("🔧 Detectado código en la respuesta, procesando...")
		
		// Process the AI response and create files
		if err := rp.fileManager.ProcessAIResponse(response); err != nil {
			fmt.Printf("❌ Error procesando archivos: %v\n", err)
			return response
		}
		
		// Check if files were created and start server if needed
		if rp.webServer.CheckIfFileExists() {
			fmt.Println("✅ Archivos creados exitosamente")
			
			// Start web server in background
			errChan := rp.webServer.StartInBackground()
			
			// Check for immediate errors
			select {
			case err := <-errChan:
				fmt.Printf("❌ Error iniciando servidor: %v\n", err)
			default:
				// Server started successfully
				response += "\n\n🌐 **Tu proyecto está disponible en:** http://localhost:1111"
			}
		} else {
			fmt.Println("❌ No se crearon archivos HTML")
		}
	} else {
		fmt.Println("DEBUG: No se detectaron bloques de código en la respuesta")
	}
	
	// Clean up the response for better formatting
	return rp.cleanResponse(response)
}

// containsCodeBlocks checks if the response contains code blocks
func (rp *ResponseProcessor) containsCodeBlocks(response string) bool {
	patterns := []string{
		"```html",
		"```css",
		"```javascript",
		"```js",
	}
	
	for _, pattern := range patterns {
		if strings.Contains(response, pattern) {
			fmt.Printf("DEBUG: Encontrado patrón: %s\n", pattern)
			return true
		}
	}
	
	fmt.Println("DEBUG: No se encontraron patrones de código")
	return false
}

// cleanResponse cleans up the response for better terminal display
func (rp *ResponseProcessor) cleanResponse(response string) string {
	// Remove code blocks for terminal display, but keep the explanation
	htmlPattern := regexp.MustCompile("(?s)```html.*?```")
	cssPattern := regexp.MustCompile("(?s)```css.*?```")
	jsPattern := regexp.MustCompile("(?s)```(?:javascript|js).*?```")
	
	cleaned := htmlPattern.ReplaceAllString(response, "[Código HTML creado en /temp/project/index.html]")
	cleaned = cssPattern.ReplaceAllString(cleaned, "[Código CSS creado en /temp/project/style.css]")
	cleaned = jsPattern.ReplaceAllString(cleaned, "[Código JavaScript creado en /temp/project/script.js]")
	
	// Clean up extra newlines
	cleaned = regexp.MustCompile(`\n{3,}`).ReplaceAllString(cleaned, "\n\n")
	
	return strings.TrimSpace(cleaned)
}