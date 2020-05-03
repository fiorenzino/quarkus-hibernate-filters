package ovh.flw.quarkusfilters.model.secondarytable;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name = "gift_details")
public class GiftDetail extends PanacheEntity {

    public Long gift_id;
    public String gift_color;
    public String gift_size;
    public String gift_model;

    @Override
    public String toString() {
        return "GiftDetail{" +
                " gift_id='" + gift_id + '\'' +
                ", gift_color='" + gift_color + '\'' +
                ", gift_size='" + gift_size + '\'' +
                ", gift_model='" + gift_model + '\'' +
                '}';
    }
}
