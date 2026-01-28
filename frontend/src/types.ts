export interface Quote {
    id: number;
    text: string;
    author: string;
    bookTitle: string;
    year: number;
    pageNumber?: number;
    upvotes: number;
    downvotes: number;
    createdAt: string;
}


export interface Response {
    id: number;
    quote: Quote;
    text: string;
    upvotes: number;
    downvotes: number;
    isEdited: boolean;
    createdAt: string;
    updatedAt: string;
}    