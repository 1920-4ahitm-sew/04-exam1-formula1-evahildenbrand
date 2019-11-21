package at.htl.formula1.boundary;

import at.htl.formula1.entity.Driver;
import at.htl.formula1.entity.Race;
import at.htl.formula1.entity.Result;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Transactional
@Path("results")
public class ResultsEndpoint {

    @PersistenceContext
    EntityManager em;


    /**
     * @param name als QueryParam einzulesen
     * @return JsonObject
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getPointsSumOfDriver(@QueryParam("name") String name) {
        TypedQuery<Driver> query = em.createNamedQuery("Driver.findByName", Driver.class).setParameter("NAME", name);

        Driver driver = query.getSingleResult();

        long sumPoints = em
                .createNamedQuery("Result.getPointSumofDriver", Long.class)
                .setParameter("DRIVER", driver)
                .getSingleResult();

        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add("driver", driver.getName());
        jsonObjectBuilder.add("points", sumPoints);

        return jsonObjectBuilder.build();
    }

    /**
     * @param country des Rennens
     * @return
     */
    @GET
    @Path("winner/{country}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findWinnerOfRace(@PathParam("country") String country) {
        Race race = em
                .createNamedQuery("Race.findByCountry", Race.class)
                .setParameter("COUNTRY", country)
                .getSingleResult();

        Driver winner = em
                .createNamedQuery("Result.getWinner", Driver.class)
                .setParameter("RACE", race)
                .getSingleResult();

        return Response.ok(winner).build();
    }


    // Ergänzen Sie Ihre eigenen Methoden ...

}
