package edu.oswego.cs.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.SortedSet;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProfanitySettings {
    SortedSet<String> blockedWords;
    SortedSet<String> acceptedWords;
}
