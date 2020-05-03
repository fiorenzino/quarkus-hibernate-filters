package ovh.flw.quarkusfilters.model.secondarytable;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.runtime.JpaOperations;
import io.quarkus.panache.common.Sort;
import org.hibernate.Session;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SqlFragmentAlias;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Map;


@Entity
@Table(name = "gifts")
@SecondaryTable(
        name = "gift_details",
        pkJoinColumns = @PrimaryKeyJoinColumn(name = "gift_id")
)
@FilterDef(name = "minId", parameters = @ParamDef(name = "minId", type = "long"))
@Filter(name = "minId", condition = "id >= :minId")

@FilterDef(name = "maxId", parameters = @ParamDef(name = "maxId", type = "long"))
@Filter(name = "maxId", condition = "id <= :maxId")

@FilterDef(name = "likeName", parameters = @ParamDef(name = "name", type = "string"))
@Filter(name = "likeName", condition = "name LIKE :name")

@FilterDef(name = "minDate", parameters = @ParamDef(name = "minDate", type = "java.time.LocalDate"))
@Filter(name = "minDate", condition = "localDate > :minDate")

@FilterDef(name = "intervalDates", parameters = {
        @ParamDef(name = "interval_minDate", type = "java.time.LocalDate"),
        @ParamDef(name = "interval_maxDate", type = "java.time.LocalDate")
})
@Filter(name = "intervalDates", condition = "localDate > :interval_minDate and localDate < :interval_maxDate")

@FilterDef(name = "onDetail", parameters = {
        @ParamDef(name = "id", type = "long"),
        @ParamDef(name = "gift_color", type = "string")
})
@Filter(
        name = "onDetail",
        condition = "{g}.id = :id and {gd}.gift_color = :gift_color",
        aliases = {
                @SqlFragmentAlias(alias = "g", table = "gifts"),
                @SqlFragmentAlias(alias = "gd", table = "gift_details"),
        }
)
public class Gift extends PanacheEntity {

    public String name;
    public LocalDate localDate;

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

    public static PanacheQuery<Gift> findByFilters(String orderBy) {
        if (orderBy != null) {
            return Gift.find("select a from Gift a", Sort.by(orderBy));
        }
        return Gift.find("select a from Gift a");
    }

    @Override
    public String toString() {
        return "Gift{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", localDate='" + localDate + '\'' +
                '}';
    }
}
