package ovh.flw.quarkusfilters.model.filterjointable;


import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.runtime.JpaOperations;
import io.quarkus.panache.common.Sort;
import org.hibernate.Session;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterJoinTable;
import org.hibernate.annotations.ParamDef;
import ovh.flw.quarkusfilters.model.secondarytable.Gift;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "clients")

@FilterDef(name = "minId", parameters = @ParamDef(name = "minId", type = "long"))
@Filter(name = "minId", condition = "id >= :minId")

@FilterDef(name = "maxId", parameters = @ParamDef(name = "maxId", type = "long"))
@Filter(name = "maxId", condition = "id <= :maxId")

@FilterDef(name = "likeName", parameters = @ParamDef(name = "name", type = "string"))
@Filter(name = "likeName", condition = "name LIKE :name")

@FilterDef(name = "minDate", parameters = @ParamDef(name = "minDate", type = "java.time.LocalDate"))
@Filter(name = "minDate", condition = "created > :minDate")

@FilterDef(name = "firstAccounts", parameters = @ParamDef(name = "maxOrderId", type = "int"))
@FilterDef(name = "accountType", parameters = @ParamDef(name = "type", type = "ovh.flw.quarkusfilters.model.filterjointable.AccountType"))


public class Client extends PanacheEntity {

    public String name;
    public LocalDate created;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderColumn(name = "order_id")
    @FilterJoinTable(
            name = "firstAccounts",
            condition = "order_id <= :maxOrderId"
    )
    @Filter(name = "accountType", condition = "account_type = :type")
    public List<Account> accounts = new ArrayList<>();


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

    public static PanacheQuery<Client> findByFilters(String orderBy) {
        if (orderBy != null) {
            return Gift.find("select a from Client a", Sort.by(orderBy));
        }
        return Gift.find("select a from Client a");
    }

    public Client addAccount(Account account) {
        this.accounts.add(account);
        return this;
    }

}
