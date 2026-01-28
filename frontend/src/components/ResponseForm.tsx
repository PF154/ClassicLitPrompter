import { useState } from "react";

interface ResponseFormProps {
    onSubmit:  (text: string) => void;
}

// The user can respond to quotes they have not already responded to through this form
function ResponseForm({ onSubmit }: ResponseFormProps) {
    const [text, setText] = useState('');

    const wordCount = text.trim() === '' ? 0 : text.trim().split(/\s+/).length;

    const handleSubmit = (e: React.FormEvent) => {
        // Don't reload the page, just submit
        e.preventDefault();

        // We want to confine the word count so that we don't get nonexistent or overly long responses
        if (wordCount > 0 && wordCount <= 750) {
            onSubmit(text);
        }
    }

    return (
        <form onSubmit={handleSubmit}>
            <textarea value={text} onChange={(e) => setText(e.target.value)}></textarea>
            <p>{wordCount} / 750 words</p>
            <button type="submit" disabled={wordCount === 0 || wordCount >750}>
                Submit
            </button>
        </form>
    )
}

export default ResponseForm;