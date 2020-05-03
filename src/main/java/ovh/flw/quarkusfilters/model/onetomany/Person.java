package ovh.flw.quarkusfilters.model.onetomany;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.runtime.JpaOperations;
import io.quarkus.panache.common.Sort;
import org.hibernate.Session;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "persons")
@FilterDef(name = "lastName", parameters = @ParamDef(name = "lastName", type = "string"))
@Filter(name = "lastName", condition = "lastName LIKE :lastName")

@FilterDef(name = "tax_code", parameters = @ParamDef(name = "tax_code", type = "string"))
@Filter(name = "tax_code", condition = "tax_code = :tax_code")

@FilterDef(name = "phones_by_brand", parameters = @ParamDef(name = "brand", type = "string"))
@FilterDef(name = "phones_by_phone_model", parameters = @ParamDef(name = "phone_model", type = "string"))
@FilterDef(name = "phones_by_mobile_operator", parameters = @ParamDef(name = "mobile_operator", type = "string"))

public class Person extends PanacheEntity {

    public String firstName;
    public String lastName;
    public String tax_code;

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Filter(name = "phones_by_brand", condition = "brand = :brand")
    @Filter(name = "phones_by_phone_model", condition = "phone_model = :phone_model")
    @Filter(name = "phones_by_mobile_operator", condition = "mobile_operator = :mobile_operator")
    public List<MobilePhone> mobilePhones = new ArrayList<>();

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

    public static PanacheQuery<Person> findByFilters(String orderBy) {
        if (orderBy != null) {
            return Person.find("select a from Person a", Sort.by(orderBy));
        }
        return Person.find("select a from Person a");
    }

    public Person add(MobilePhone mobilePhone) {
        this.mobilePhones.add(mobilePhone);
        return this;
    }
}
