package coin_app;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.*;

@Path("/coins")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CoinResource {

    @POST
    public List<Double> calculateCoins(CoinRequest request) {
        double targetAmount = request.getTargetAmount();
        List<Double> denominations = request.getDenominations();
        Set<Double> validDenominations = new HashSet<>(Arrays.asList(
                0.01, 0.05, 0.1, 0.2, 0.5, 1.0, 2.0, 5.0, 10.0, 50.0, 100.0, 1000.0
        ));
        for (double denomination : denominations) {
            if (!validDenominations.contains(denomination)) {
                throw new WebApplicationException("Invalid coin denomination: " + denomination, 400);
            }
        }
        Collections.sort(denominations, Collections.reverseOrder());
        List<Double> result = new ArrayList<>();
        for (double coin : denominations) {
            while (targetAmount >= coin) {
                targetAmount = Math.round((targetAmount - coin) * 100.0) / 100.0;
                result.add(coin);
            }
        }
        if (targetAmount > 0) {
            throw new WebApplicationException("Unable to make up the target amount with the given denominations", 400);
        }
        Collections.sort(result);
        return result;
    }
}
