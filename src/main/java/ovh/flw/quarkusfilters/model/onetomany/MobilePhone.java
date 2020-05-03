package ovh.flw.quarkusfilters.model.onetomany;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.runtime.JpaOperations;
import io.quarkus.panache.common.Sort;
import org.hibernate.Session;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Map;

@Entity
@Table(name = "mobilephones")

@FilterDef(name = "mobile_operator", parameters = @ParamDef(name = "mobile_operator", type = "string"))
@Filter(name = "mobile_operator", condition = "mobile_operator = :mobile_operator")

@FilterDef(name = "brand", parameters = @ParamDef(name = "brand", type = "string"))
@Filter(name = "brand", condition = "brand = :brand")

@FilterDef(name = "phone_model", parameters = @ParamDef(name = "phone_model", type = "string"))
@Filter(name = "phone_model", condition = "phone_model = :phone_model")

@FilterDef(name = "person_id", parameters = @ParamDef(name = "person_id", type = "integer"))
@Filter(name = "person_id", condition = "person_id = :person_id")

@FilterDef(name = "owner_by_tax_code", parameters = @ParamDef(name = "tax_code", type = "string"))
@Filter(name = "owner_by_tax_code", condition = "person_id in (select p.id from persons p where p.tax_code = :tax_code)")

public class MobilePhone extends PanacheEntity {

    public LocalDate purchase_date;
    public String mobile_operator;
    public String brand;
    @OrderBy
    public String phone_model;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    @JsonBackReference
    public Person owner;

    public static void enableFilter(String filterName, String parameterName, Object value) {
        JpaOperations.getEntityManager().unwrap(Session.class)
                .enableFilter(filterName)
                .setParameter(parameterName, value);
    }

    public static void enableFilter(String filterName, Map<String, Object> params) {
        JpaOperations.getEntityManager().unwrap(Session.class)
                .enableFilter(filterName);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            JpaOperations.getEntityManager().unwrap(Session.class)
                    .getEnabledFilter(filterName)
                    .setParameter(entry.getKey(), entry.getValue());
        }

    }

    public static PanacheQuery<MobilePhone> findByFilters(String orderBy) {
        if (orderBy != null) {
            return MobilePhone.find("select a from MobilePhone a", Sort.by(orderBy));
        }
        return MobilePhone.find("select a from MobilePhone a");
    }

    public MobilePhone owner(Person owner) {
        this.owner = owner;
        return this;
    }

    public MobilePhone purchase_date(LocalDate purchase_date) {
        this.purchase_date = purchase_date;
        return this;
    }

    public MobilePhone mobile_operator(String mobile_operator) {
        this.mobile_operator = mobile_operator;
        return this;
    }

    public MobilePhone brand(String brand) {
        this.brand = brand;
        return this;
    }

    public MobilePhone phone_model(String phone_model) {
        this.phone_model = phone_model;
        return this;
    }

    @Override
    public String toString() {
        return "MobilePhone{" +
                "purchase_date=" + purchase_date +
                ", mobile_operator='" + mobile_operator + '\'' +
                ", brand='" + brand + '\'' +
                ", phone_model='" + phone_model + '\'' +
                ", owner=" + owner +
                '}';
    }
}
