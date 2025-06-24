package openrouter

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"time"
)

// Message represents a chat message.
type Message struct {
	Role    string `json:"role"`
	Content string `json:"content"`
}

// chatRequest is the payload sent to OpenRouter.
type chatRequest struct {
	Model       string    `json:"model"`
	Messages    []Message `json:"messages"`
	MaxTokens   int       `json:"max_tokens,omitempty"`
	Temperature float64   `json:"temperature,omitempty"`
}

// chatResponse is the API response structure.
type chatResponse struct {
	Choices []struct {
		Message Message `json:"message"`
	} `json:"choices"`
	Error struct {
		Message string `json:"message"`
		Code    string `json:"code"`
	} `json:"error,omitempty"`
}

// Client handles requests to the OpenRouter API.
type Client struct {
	apiKey     string
	baseURL    string
	httpClient *http.Client
}

// NewClient returns an OpenRouter API client.
func NewClient(apiKey string) *Client {
	return &Client{
		apiKey:  apiKey,
		baseURL: "https://openrouter.ai/api/v1",
		httpClient: &http.Client{
			Timeout: 60 * time.Second,
		},
	}
}

// Chat sends a message with debug output.
func (c *Client) Chat(model, context, message string) (string, error) {
	return c.chat(model, context, message, true)
}

// ChatQuiet sends a message without debug output.
func (c *Client) ChatQuiet(model, context, message string) (string, error) {
	return c.chat(model, context, message, false)
}

// chat is the internal method that handles both debug and quiet modes.
func (c *Client) chat(model, context, message string, debug bool) (string, error) {
	if debug {
		fmt.Printf("DEBUG: Using model: %s\n", model)
		fmt.Printf("DEBUG: API Key length: %d\n", len(c.apiKey))
	}

	var messages []Message
	if context != "" {
		messages = append(messages, Message{Role: "system", Content: context})
	}
	messages = append(messages, Message{Role: "user", Content: message})

	reqBody := chatRequest{
		Model:       model,
		Messages:    messages,
		MaxTokens:   10000,
		Temperature: 0.7,
	}

	data, err := json.Marshal(reqBody)
	if err != nil {
		return "", fmt.Errorf("failed to marshal request: %v", err)
	}

	if debug {
		fmt.Printf("DEBUG: Request body: %s\n", string(data))
	}

	req, err := http.NewRequest("POST", c.baseURL+"/chat/completions", bytes.NewReader(data))
	if err != nil {
		return "", fmt.Errorf("failed to create request: %v", err)
	}

	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("Authorization", "Bearer "+c.apiKey)
	req.Header.Set("HTTP-Referer", "https://github.com/cordlang/runix")
	req.Header.Set("X-Title", "runix-cli")

	if debug {
		fmt.Printf("DEBUG: Making request to: %s\n", req.URL.String())
	}

	resp, err := c.httpClient.Do(req)
	if err != nil {
		return "", fmt.Errorf("request failed: %v", err)
	}
	defer resp.Body.Close()

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return "", fmt.Errorf("failed to read response: %v", err)
	}

	if debug {
		fmt.Printf("DEBUG: Response status: %d\n", resp.StatusCode)
		fmt.Printf("DEBUG: Response body: %s\n", string(body))
	}

	if resp.StatusCode != http.StatusOK {
		return "", fmt.Errorf("openrouter error (status %d): %s", resp.StatusCode, string(body))
	}

	var r chatResponse
	if err := json.Unmarshal(body, &r); err != nil {
		return "", fmt.Errorf("failed to parse response: %v", err)
	}

	if r.Error.Message != "" {
		return "", fmt.Errorf("API error: %s (code: %s)", r.Error.Message, r.Error.Code)
	}

	if debug {
		fmt.Printf("DEBUG: Choices count: %d\n", len(r.Choices))
	}

	if len(r.Choices) == 0 {
		return "", fmt.Errorf("no choices returned - response: %s", string(body))
	}

	return r.Choices[0].Message.Content, nil
}
