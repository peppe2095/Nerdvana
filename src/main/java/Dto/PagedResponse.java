package Dto;

import java.util.List;

public class PagedResponse<T> {
    public List<T> items;
    public int page;
    public int size;
    public int total;
    public int totalPages;

    public PagedResponse() {}

    public PagedResponse(List<T> items, int page, int size, int total, int totalPages) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.total = total;
        this.totalPages = totalPages;
    }
}