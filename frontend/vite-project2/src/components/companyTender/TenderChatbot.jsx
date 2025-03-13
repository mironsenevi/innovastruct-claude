import React, { useState, useRef, useEffect } from 'react';
import { MessageCircle, Send, X, Minimize2, Maximize2, Bot } from 'lucide-react';

const mockResponses = {
  default: "I can help you find tenders based on location, budget, or deadline. What would you like to know?",
  greeting: "Hello! I'm your tender assistant. How can I help you today?",
  location: "I can show you tenders in that area. Would you like to apply any other filters?",
  budget: "I'll filter tenders within that budget range. Would you like to see the results?",
  deadline: "I'll show you tenders with those deadline requirements. Anything else you'd like to know?",
  notUnderstood: "I'm not sure I understood that. Could you rephrase your question?"
};

const TenderChatbot = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [isMinimized, setIsMinimized] = useState(false);
  const [messages, setMessages] = useState([
    { type: 'bot', content: mockResponses.greeting, timestamp: new Date() }
  ]);
  const [inputValue, setInputValue] = useState('');
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    if (isOpen) {
      scrollToBottom();
    }
  }, [messages, isOpen]);

  const handleSend = () => {
    if (!inputValue.trim()) return;

    // Add user message
    const newMessages = [...messages, { 
      type: 'user', 
      content: inputValue, 
      timestamp: new Date() 
    }];
    setMessages(newMessages);
    setInputValue('');

    // Simulate bot response
    setTimeout(() => {
      let response = mockResponses.default;
      const lowercaseInput = inputValue.toLowerCase();

      if (lowercaseInput.includes('location') || lowercaseInput.includes('area')) {
        response = mockResponses.location;
      } else if (lowercaseInput.includes('budget') || lowercaseInput.includes('cost')) {
        response = mockResponses.budget;
      } else if (lowercaseInput.includes('deadline') || lowercaseInput.includes('due')) {
        response = mockResponses.deadline;
      }

      setMessages(prev => [...prev, { 
        type: 'bot', 
        content: response, 
        timestamp: new Date() 
      }]);
    }, 1000);
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  if (!isOpen) {
    return (
      <button
        onClick={() => setIsOpen(true)}
        className="fixed bottom-6 right-6 bg-yellow-500 text-white p-4 rounded-full shadow-lg 
          hover:bg-yellow-600 transition-colors duration-200 z-50"
      >
        <MessageCircle className="w-6 h-6" />
      </button>
    );
  }

  return (
    <div className={`fixed bottom-6 right-6 w-96 bg-white rounded-2xl shadow-2xl z-50 
      transition-all duration-300 ${isMinimized ? 'h-14' : 'h-[600px]'}`}>
      {/* Header */}
      <div className="flex items-center justify-between p-4 border-b bg-yellow-500 rounded-t-2xl">
        <div className="flex items-center gap-2 text-white">
          <Bot className="w-6 h-6" />
          <h3 className="font-semibold">Tender Assistant</h3>
        </div>
        <div className="flex items-center gap-2">
          <button
            onClick={() => setIsMinimized(!isMinimized)}
            className="p-1 hover:bg-yellow-600 rounded-lg transition-colors"
          >
            {isMinimized ? 
              <Maximize2 className="w-5 h-5 text-white" /> : 
              <Minimize2 className="w-5 h-5 text-white" />
            }
          </button>
          <button
            onClick={() => setIsOpen(false)}
            className="p-1 hover:bg-yellow-600 rounded-lg transition-colors"
          >
            <X className="w-5 h-5 text-white" />
          </button>
        </div>
      </div>

      {!isMinimized && (
        <>
          {/* Messages Container */}
          <div className="h-[472px] overflow-y-auto p-4 space-y-4">
            {messages.map((message, index) => (
              <div
                key={index}
                className={`flex ${message.type === 'user' ? 'justify-end' : 'justify-start'}`}
              >
                <div className={`max-w-[80%] rounded-lg p-3 ${
                  message.type === 'user' 
                    ? 'bg-yellow-500 text-white' 
                    : 'bg-gray-100 text-gray-800'
                }`}>
                  <p className="text-sm">{message.content}</p>
                  <span className="text-xs opacity-75 mt-1 block">
                    {message.timestamp.toLocaleTimeString([], { 
                      hour: '2-digit', 
                      minute: '2-digit' 
                    })}
                  </span>
                </div>
              </div>
            ))}
            <div ref={messagesEndRef} />
          </div>

          {/* Input Area */}
          <div className="p-4 border-t bg-gray-50 rounded-b-2xl">
            <div className="flex items-center gap-2">
              <input
                type="text"
                value={inputValue}
                onChange={(e) => setInputValue(e.target.value)}
                onKeyPress={handleKeyPress}
                placeholder="Type your message..."
                className="flex-1 rounded-lg border-gray-300 focus:ring-yellow-500 
                  focus:border-yellow-500 text-sm"
              />
              <button
                onClick={handleSend}
                disabled={!inputValue.trim()}
                className="p-2 bg-yellow-500 text-white rounded-lg hover:bg-yellow-600 
                  disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                <Send className="w-5 h-5" />
              </button>
            </div>
          </div>
        </>
      )}
    </div>
  );
};

export default TenderChatbot;