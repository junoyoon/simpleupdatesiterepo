package ru.lanwen.jenkins.juseppe.gen;

import java.util.Collections;
import java.util.List;

/**
 * @author lanwen (Merkushev Kirill)
 */
public class SavableSitesCollection {

    private final List<SavableSite> savables;

    public SavableSitesCollection(List<SavableSite> savables) {
        this.savables = Collections.unmodifiableList(savables);
    }

    public List<SavableSite> savables() {
        return savables;
    }

    public void saveAll() {
        savables.forEach(SavableSite::save);
    }
}
