package ch.zhaw.fswd.powerdate.dto;

import ch.zhaw.fswd.powerdate.entity.InterestDbo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterestDto {
    private Long id;
    private String name;
    private String examples;

    public InterestDto fromDbo(InterestDbo dbo) {
        this.id = dbo.getId();
        this.name = dbo.getName();
        this.examples = dbo.getExamples();
        return this;
    }

    public InterestDbo toDbo() {
        InterestDbo dbo = new InterestDbo();
        dbo.setId(this.id);
        dbo.setName(this.name);
        dbo.setExamples(this.examples);
        return dbo;
    }
}
