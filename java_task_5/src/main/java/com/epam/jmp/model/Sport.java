package com.epam.jmp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "sports")
public class Sport implements Persistable<String> {

    @Id
    private String id;

    @TextIndexed
    private String sportName;

    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

    public Sport(String id, String sportName) {
        this.id = id;
        this.sportName = sportName;
    }

    public Sport setNew(boolean isNew) {
        this.isNew = isNew;
        return this;
    }

    public Sport merge(Sport sport) {
        if (sport.getSportName() != null) {
            this.sportName = sport.getSportName();
        }
        return this;
    }
}
