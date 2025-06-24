package openrouter

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
)

type Message struct {
	Role    string `json:"role"`
	Content string `json:"content"`
}

type chatRequest struct {
	Model    string    `json:"model"`
	Messages []Message `json:"messages"`
}

type chatResponse struct {
	Choices []struct {
		Message Message `json:"message"`
	} `json:"choices"`
}

type Client struct {
	apiKey     string
	baseURL    string
	httpClient *http.Client
}

// NewClient returns an OpenRouter API client.
func NewClient(apiKey string) *Client {
	return &Client{
		apiKey:     apiKey,
		baseURL:    "https://openrouter.ai/api/v1",
		httpClient: &http.Client{},
	}
}

// Chat sends a message with an optional context prompt to the given model.
func (c *Client) Chat(model, context, message string) (string, error) {
	reqBody := chatRequest{
		Model: model,
		Messages: []Message{
			{Role: "system", Content: context},
			{Role: "user", Content: message},
		},
	}
	data, err := json.Marshal(reqBody)
	if err != nil {
		return "", err
	}

	req, err := http.NewRequest("POST", c.baseURL+"/chat/completions", bytes.NewReader(data))
	if err != nil {
		return "", err
	}
	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("Authorization", "Bearer "+c.apiKey)
	req.Header.Set("HTTP-Referer", "https://github.com/cordlang/runix")
	req.Header.Set("User-Agent", "runix-cli")

	resp, err := c.httpClient.Do(req)
	if err != nil {
		return "", err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		body, _ := io.ReadAll(resp.Body)
		return "", fmt.Errorf("openrouter error: %s", string(body))
	}

	var r chatResponse
	if err := json.NewDecoder(resp.Body).Decode(&r); err != nil {
		return "", err
	}
	if len(r.Choices) == 0 {
		return "", fmt.Errorf("no choices returned")
	}
	return r.Choices[0].Message.Content, nil
}
