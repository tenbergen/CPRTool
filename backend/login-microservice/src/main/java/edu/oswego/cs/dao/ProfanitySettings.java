package edu.oswego.cs.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

@NoArgsConstructor
@Getter
@Setter
public class ProfanitySettings {
    ArrayList<String> blockedWords;
    ArrayList<String> acceptedWords;

    public ProfanitySettings(ArrayList<String> blockedWords, ArrayList<String> acceptedWords) {
        this.blockedWords = new ArrayList<>(blockedWords);
        this.acceptedWords = new ArrayList<>(acceptedWords);
    }
}
