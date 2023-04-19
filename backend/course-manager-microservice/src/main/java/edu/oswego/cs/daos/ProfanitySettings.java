package edu.oswego.cs.daos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProfanitySettings {
    private ArrayList<String> words;

    public ProfanitySettings(List<String> blockedWords) {
        this.words = new ArrayList<>(blockedWords);
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public void setWords(ArrayList<String> words) {
        this.words = words;
    }
}
