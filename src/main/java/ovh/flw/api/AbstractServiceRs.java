package ovh.flw.api;


import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;

import javax.transaction.Transactional;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractServiceRs<T> {

    @Context
    UriInfo ui;

    @SuppressWarnings("unchecked")
    protected <T> T cast(String key, Class<T> clazz) {
        String value = ui.getQueryParameters().getFirst(key);
        if (Long.class.equals(clazz)) {
            return (T) Long.valueOf(value);
        }
        if (Integer.class.equals(clazz)) {
            return (T) Integer.valueOf(value);
        }
        if (Boolean.class.equals(clazz)) {
            return (T) Boolean.valueOf(value);
        }
        if (Double.class.equals(clazz)) {
            return (T) Double.valueOf(value);
        }
        if (LocalDate.class.equals(clazz)) {
            return (T) LocalDate.parse(value);
        }
        return (T) value;
    }

    protected boolean nn(String key) {
        return ui.getQueryParameters().containsKey(key)
                && ui.getQueryParameters().getFirst(key) != null
                && !ui.getQueryParameters().getFirst(key).trim().isEmpty();
    }

    protected String get(String key) {
        return ui.getQueryParameters().getFirst(key);
    }

    private List<T> paginate(PanacheQuery<T> search, Integer startRow, Integer pageSize) {
        long listSize = search.count();
        if (listSize == 0) {
            return new ArrayList<>();
        } else {
            int currentPage = 0;
            if (pageSize != 0) {
                currentPage = startRow / pageSize;
            } else {
                pageSize = Long.valueOf(listSize).intValue();
            }
            return search.page(Page.of(currentPage, pageSize)).list();
        }
    }

    @GET
    @Transactional
    public Response getList(
            @DefaultValue("0") @QueryParam("startRow") Integer startRow,
            @DefaultValue("10") @QueryParam("pageSize") Integer pageSize,
            @QueryParam("orderBy") String orderBy) {
        try {
            PanacheQuery<T> search = filter(ui, orderBy);
            List<T> list = paginate(search, startRow, pageSize);
            return Response.ok(list).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    public abstract PanacheQuery<T> filter(UriInfo uriInfo, String orderBy);

}
