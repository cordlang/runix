package openrouter

import (
	"bufio"
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"strings"
)

type Message struct {
	Role    string `json:"role"`
	Content string `json:"content"`
}

type chatRequest struct {
	Model    string    `json:"model"`
	Messages []Message `json:"messages"`
	Stream   bool      `json:"stream,omitempty"`
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

// ChatStream sends a message and streams the response tokens.
func (c *Client) ChatStream(model, context, message string) (<-chan string, error) {
	reqBody := chatRequest{
		Model:  model,
		Stream: true,
		Messages: []Message{
			{Role: "system", Content: context},
			{Role: "user", Content: message},
		},
	}
	data, err := json.Marshal(reqBody)
	if err != nil {
		return nil, err
	}

	req, err := http.NewRequest("POST", c.baseURL+"/chat/completions", bytes.NewReader(data))
	if err != nil {
		return nil, err
	}
	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("Authorization", "Bearer "+c.apiKey)
	req.Header.Set("HTTP-Referer", "https://github.com/cordlang/runix")
	req.Header.Set("User-Agent", "runix-cli")

	resp, err := c.httpClient.Do(req)
	if err != nil {
		return nil, err
	}

	if resp.StatusCode != http.StatusOK {
		body, _ := io.ReadAll(resp.Body)
		resp.Body.Close()
		return nil, fmt.Errorf("openrouter error: %s", string(body))
	}

	ch := make(chan string)

	go func() {
		defer resp.Body.Close()
		defer close(ch)

		reader := bufio.NewReader(resp.Body)
		for {
			line, err := reader.ReadString('\n')
			if err != nil {
				if err != io.EOF {
					fmt.Println("stream error:", err)
				}
				return
			}

			line = strings.TrimSpace(line)
			if line == "" || line == "data: [DONE]" {
				if line == "data: [DONE]" {
					return
				}
				continue
			}

			if strings.HasPrefix(line, "data: ") {
				var payload struct {
					Choices []struct {
						Delta struct {
							Content string `json:"content"`
						} `json:"delta"`
					} `json:"choices"`
				}
				if err := json.Unmarshal([]byte(strings.TrimPrefix(line, "data: ")), &payload); err == nil {
					if len(payload.Choices) > 0 {
						token := payload.Choices[0].Delta.Content
						if token != "" {
							ch <- token
						}
					}
				}
			}
		}
	}()

	return ch, nil
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

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return "", err
	}

	if resp.StatusCode != http.StatusOK {
		return "", fmt.Errorf("openrouter error: %s", string(body))
	}

	var r chatResponse
	if err := json.Unmarshal(body, &r); err != nil {
		return "", err
	}
	if len(r.Choices) == 0 {
		return "", fmt.Errorf("no choices returned: %s", string(body))
	}
	return r.Choices[0].Message.Content, nil
}
