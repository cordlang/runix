package filemanager

import (
	"fmt"
	"os"
	"path/filepath"
	"regexp"
	"strings"
)

type FileManager struct {
	TempDir    string
	ProjectDir string
}

// NewFileManager creates a new file manager with temp directories
func NewFileManager() (*FileManager, error) {
	tempDir := filepath.Join(os.TempDir(), "runix_temp")
	projectDir := filepath.Join(tempDir, "project")
	
	// Create directories if they don't exist
	if err := os.MkdirAll(projectDir, 0755); err != nil {
		return nil, err
	}
	
	return &FileManager{
		TempDir:    tempDir,
		ProjectDir: projectDir,
	}, nil
}

// ProcessAIResponse processes the AI response and extracts code blocks
func (fm *FileManager) ProcessAIResponse(response string) error {
	// Extract HTML code blocks
	htmlBlocks := fm.extractCodeBlocks(response, "html")
	
	// If HTML found, create index.html
	if len(htmlBlocks) > 0 {
		indexPath := filepath.Join(fm.ProjectDir, "index.html")
		return fm.writeFile(indexPath, htmlBlocks[0])
	}
	
	// Extract CSS code blocks
	cssBlocks := fm.extractCodeBlocks(response, "css")
	if len(cssBlocks) > 0 {
		cssPath := filepath.Join(fm.ProjectDir, "style.css")
		return fm.writeFile(cssPath, cssBlocks[0])
	}
	
	// Extract JavaScript code blocks
	jsBlocks := fm.extractCodeBlocks(response, "javascript", "js")
	if len(jsBlocks) > 0 {
		jsPath := filepath.Join(fm.ProjectDir, "script.js")
		return fm.writeFile(jsPath, jsBlocks[0])
	}
	
	return nil
}

// extractCodeBlocks extracts code blocks from markdown-style code fences
func (fm *FileManager) extractCodeBlocks(text string, languages ...string) []string {
	var blocks []string
	
	for _, lang := range languages {
		// Pattern for code blocks: ```language\ncode\n```
		pattern := fmt.Sprintf("(?s)```%s\\s*\\n(.*?)\\n```", lang)
		re := regexp.MustCompile(pattern)
		matches := re.FindAllStringSubmatch(text, -1)
		
		for _, match := range matches {
			if len(match) > 1 {
				blocks = append(blocks, strings.TrimSpace(match[1]))
			}
		}
	}
	
	return blocks
}

// writeFile writes content to a file
func (fm *FileManager) writeFile(path, content string) error {
	fmt.Printf("📄 Creando archivo: %s\n", filepath.Base(path))
	return os.WriteFile(path, []byte(content), 0644)
}

// GetProjectPath returns the project directory path
func (fm *FileManager) GetProjectPath() string {
	return fm.ProjectDir
}

// Cleanup removes the temporary directory
func (fm *FileManager) Cleanup() error {
	return os.RemoveAll(fm.TempDir)
}