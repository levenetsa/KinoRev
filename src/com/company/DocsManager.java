package com.company;

import java.util.ArrayList;
import java.util.List;

public class DocsManager {
    private List<Doc> documents;

    public DocsManager(){
        this.documents = new ArrayList<>();
    }
    public void attachDocs(List<Doc> documents) {
            this.documents.addAll(documents);
    }
}
