package com.auca.library.dao;

import com.auca.library.domain.Book;

public class BookDao extends AbstractDao<Book> {

    public BookDao() {
        super(Book.class);
    }
}
