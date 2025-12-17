import { useState, useRef, useEffect } from 'react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { useAppSelector, useAppDispatch } from '@/hooks'
import { RootState } from '@/redux/store'
import { MessageCircle, X, Send, User, Loader2 } from 'lucide-react'
import { messageApi, Conversation as ApiConversation } from '@/services/api/messageApi'
import { setMessages } from '@/redux/slices/chat'

interface Message {
    id: string | number
    type: 'user' | 'seller'
    message: string
    timestamp: Date
}

const SellerChatModal = () => {
    const [isOpen, setIsOpen] = useState(false)
    const [conversation, setConversation] = useState<ApiConversation | null>(null)
    const [inputValue, setInputValue] = useState('')
    const [isLoading, setIsLoading] = useState(false)
    const messagesEndRef = useRef<HTMLDivElement>(null)
    const hasFetchedRef = useRef(false)

    const dispatch = useAppDispatch()
    const isLogin = useAppSelector((state: RootState) => state.auth.isLogin)
    const currentUsername = useAppSelector((state: RootState) => state.user.info?.userName)

    // ✅ Lấy messages từ Redux
    // Trong SellerChatModal.tsx
    const messagesFromRedux = useAppSelector((state: RootState) => {
        const msgs = conversation ? state.chat.messages[conversation.id] || [] : [];
        console.log(`📬 SellerChatModal - Redux messages:`, msgs.length, msgs);
        return msgs;
    });

    // ✅ Convert sang format hiển thị
    const messages: Message[] = messagesFromRedux.map(msg => ({
        id: msg.id,
        type: msg.sender?.userName === currentUsername ? 'user' : 'seller',
        message: msg.message || msg.content,
        timestamp: new Date(msg.createdDate)
    }))

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
    }

    const handleFetchConversation = async () => {
        if (hasFetchedRef.current) return;

        try {
            hasFetchedRef.current = true;
            const conversations = await messageApi.getMyConversations();

            if (conversations && conversations.length > 0) {
                setConversation(conversations[0]);
                const msgs = await messageApi.getMessages(conversations[0].id);

                // ✅ Dispatch vào Redux thay vì setState
                const formattedMsgs = msgs.map((msg: any) => ({
                    id: msg.id,
                    conversationId: conversations[0].id,
                    sender: msg.sender,
                    content: msg.message || msg.content,
                    message: msg.message || msg.content,
                    createdDate: msg.createdDate > 1e12 ? msg.createdDate : msg.createdDate * 1000,
                    me: msg.sender?.userName === currentUsername
                }));

                dispatch(setMessages({
                    conversationId: conversations[0].id,
                    messages: formattedMsgs
                }));
            } else {
                setConversation(null);
            }
        } catch (error) {
            console.error('Error fetching conversation:', error);
            setConversation(null);
        }
    }

    useEffect(() => {
        scrollToBottom()
    }, [messages])

    useEffect(() => {
        if (!isOpen) {
            hasFetchedRef.current = false;
            return;
        }
        handleFetchConversation();
    }, [isOpen])

    const handleSendMessage = async () => {
        if (!inputValue.trim() || isLoading || !conversation) return;

        setIsLoading(true);
        try {
            await messageApi.sendMessage(conversation.id, inputValue.trim());
            setInputValue('');
            // ✅ Socket sẽ tự động nhận và Redux sẽ update
        } catch (error) {
            console.error('Error sending message:', error);
        } finally {
            setIsLoading(false);
        }
    }

    const handleSuggestedQuestion = (question: string) => {
        setInputValue(question)
    }

    const handleKeyPress = (e: React.KeyboardEvent) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault()
            handleSendMessage()
        }
    }


    if (!isLogin) return null

    return (
        <>
            <button
                onClick={() => setIsOpen(true)}
                className={`fixed bottom-6 right-24 z-50 w-14 h-14 bg-gradient-to-r from-orange-500 to-yellow-500 rounded-full shadow-lg hover:shadow-xl transition-all duration-300 flex items-center justify-center text-white hover:scale-110 ${isOpen ? 'hidden' : ''}`}
            >
                <MessageCircle className="w-6 h-6" />
                <span className="absolute -top-1 -right-1 w-4 h-4 bg-red-500 rounded-full animate-pulse"></span>
            </button>

            {isOpen && (
                <div className="fixed bottom-6 right-24 z-50 w-96 h-[600px] bg-white rounded-2xl shadow-2xl flex flex-col overflow-hidden border border-gray-200">
                    <div className="bg-gradient-to-r from-orange-500 to-yellow-500 text-white p-4 flex items-center justify-between">
                        <div className="flex items-center gap-3">
                            <div className="w-10 h-10 rounded-full bg-white/30 flex items-center justify-center">
                                <User className="w-6 h-6 text-orange-600" />
                            </div>
                            <div>
                                <h3 className="font-semibold">Người bán</h3>
                                <p className="text-xs text-white/80">Hỗ trợ khách hàng</p>
                            </div>
                        </div>
                        <button
                            onClick={() => setIsOpen(false)}
                            className="w-8 h-8 rounded-full bg-white/20 hover:bg-white/30 flex items-center justify-center transition-colors"
                        >
                            <X className="w-4 h-4" />
                        </button>
                    </div>

                    <div className="flex-1 overflow-y-auto p-4 space-y-4 bg-gray-50">
                        {messages.length === 0 && (
                            <div className="text-center text-gray-400">Hãy trò chuyện để bắt đầu.</div>
                        )}
                        {messages.map((message) => (
                            <div
                                key={message.id}
                                className={`flex items-start gap-2 ${message.type === 'user' ? 'flex-row-reverse' : ''}`}
                            >
                                {message.type === 'user' ? (
                                    <div className="w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0 bg-orange-500 text-white">
                                        <User className="w-4 h-4" />
                                    </div>
                                ) : (
                                    <div className="w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0 bg-yellow-400 text-white">
                                        <User className="w-4 h-4 text-orange-700" />
                                    </div>
                                )}
                                <div className={`max-w-[80%] p-3 rounded-2xl ${message.type === 'user'
                                    ? 'bg-orange-500 text-white rounded-br-md'
                                    : 'bg-white text-gray-800 rounded-bl-md shadow-sm border'
                                    }`}>
                                    <p className="text-sm whitespace-pre-wrap">{message.message}</p>
                                    <span className={`text-xs mt-1 block ${message.type === 'user' ? 'text-orange-200' : 'text-gray-400'}`}>
                                        {message.timestamp.toLocaleString('vi-VN')}
                                    </span>
                                </div>
                            </div>
                        ))}
                        {isLoading && (
                            <div className="flex items-start gap-2">
                                <div className="w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0 bg-yellow-400 text-white">
                                    <User className="w-4 h-4 text-orange-700" />
                                </div>
                                <div className="bg-white p-3 rounded-2xl rounded-bl-md shadow-sm border">
                                    <div className="flex items-center gap-2">
                                        <Loader2 className="w-4 h-4 animate-spin text-orange-500" />
                                        <span className="text-sm text-gray-500">Đang chờ phản hồi...</span>
                                    </div>
                                </div>
                            </div>
                        )}
                        <div ref={messagesEndRef} />
                    </div>

                    {messages.length <= 1 && (
                        <div className="px-4 py-2 bg-white border-t">
                            <p className="text-xs text-gray-500 mb-2">Câu hỏi gợi ý:</p>
                            <div className="flex flex-wrap gap-1">
                                {['Sản phẩm này còn hàng không?', 'Thời gian giao hàng dự kiến?', 'Có bảo hành không?'].map((question, index) => (
                                    <button
                                        key={index}
                                        onClick={() => handleSuggestedQuestion(question)}
                                        className="text-xs px-2 py-1 bg-gray-100 hover:bg-gray-200 rounded-full text-gray-700 transition-colors"
                                    >
                                        {question}
                                    </button>
                                ))}
                            </div>
                        </div>
                    )}

                    <div className="p-4 bg-white border-t">
                        <div className="flex gap-2">
                            <Input
                                value={inputValue}
                                onChange={(e) => setInputValue(e.target.value)}
                                onKeyPress={handleKeyPress}
                                placeholder="Nhập tin nhắn cho người bán..."
                                className="flex-1 rounded-full border-gray-300 focus:border-orange-500"
                                disabled={isLoading}
                            />
                            <Button
                                onClick={handleSendMessage}
                                disabled={!inputValue.trim() || isLoading}
                                className="w-10 h-10 rounded-full bg-gradient-to-r from-orange-500 to-yellow-500 hover:opacity-90 p-0"
                            >
                                <Send className="w-4 h-4" />
                            </Button>
                        </div>
                    </div>
                </div>
            )}
        </>
    )
}

export default SellerChatModal