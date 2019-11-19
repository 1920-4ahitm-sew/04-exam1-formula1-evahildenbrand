package at.htl.formula1.boundary;

import at.htl.formula1.entity.Driver;
import at.htl.formula1.entity.Race;
import at.htl.formula1.entity.Result;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

@ApplicationScoped
@Transactional
public class ResultsRestClient {


    public static final String RESULTS_ENDPOINT = "http://vm90.htl-leonding.ac.at/results";
    private Client client;
    private WebTarget target;

    @PersistenceContext
    EntityManager em;

    /**
     * Vom RestEndpoint werden alle Result abgeholt und in ein JsonArray gespeichert.
     * Dieses JsonArray wird an die Methode persistResult(...) übergeben
     */
    public void readResultsFromEndpoint() {
        this.client = ClientBuilder.newClient();
        this.target = client.target(RESULTS_ENDPOINT);

        Response response = target.request().get();

        JsonArray payload = response.readEntity(JsonArray.class);

        persistResult(payload);
    }

    /**
     * Das JsonArray wird durchlaufen (iteriert). Man erhäjt dabei Objekte vom
     * Typ JsonValue. diese werden mit der Methode .asJsonObject() in ein
     * JsonObject umgewandelt.
     *
     * zB:
     * for (JsonValue jsonValue : resultsJson) {
     *             JsonObject resultJson = jsonValue.asJsonObject();
     *             ...
     *
     *  Mit den entsprechenden get-Methoden können nun die einzelnen Werte
     *  (raceNo, position und driverFullName) ausgelesen werden.
     *
     *  Mit dem driverFullName wird der entsprechende Driver aus der Datenbank ausgelesen.
     *
     *  Dieser Driver wird dann dem neu erstellten Result-Objekt übergeben
     *
     * @param resultsJson
     */
    void persistResult(JsonArray resultsJson) {
        for (JsonValue value:resultsJson) {
            JsonObject object = value.asJsonObject();

            //String driverName = object.get("driverFullName").toString();
            String driverName = object.getString("driverFullName");

            TypedQuery<Driver> query = em
                    .createNamedQuery("Driver.findByName", Driver.class)
                    .setParameter("NAME", driverName);

            //Driver currentDriver = query.getSingleResult();
            //System.out.println(currentDriver);

            List<Driver> drivers = query.getResultList();
            if (drivers.size() >= 1) {
                Driver currentDriver = drivers.get(0);
                System.out.println(currentDriver);

                //String raceId = object.get("raceNo").toString();
                //int raceNumber = Integer.parseInt(raceId);

                int raceId = object.getInt("raceNo");
                long raceNumber = (long) raceId;

                Race currentRace = this.em
                        .createNamedQuery("Race.findById", Race.class)
                        .setParameter("ID", raceNumber)
                        .getSingleResult();

//            String currentPosition = object.get("driverFullName").toString();
//            int currentPos = Integer.parseInt(currentPosition);
//
//              Result currentResult = new Result();
//              currentResult.setRace(currentRace);
//              currentResult.setPosition(currentPos);
//              currentResult.setDriver(currentDriver);
//              currentResult.setPoints(currentPos);
//
//            this.em.persist(currentResult);
//            System.out.println(object);
            }
        }
    }

}
