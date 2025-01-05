package coin_app;

import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CoinResourceTest {

    private final CoinResource coinResource = new CoinResource();

    @Test
    void testCalculateCoins_Success() {
        // Arrange
        CoinRequest request = new CoinRequest();
        request.setTargetAmount(7.03);
        request.setDenominations(Arrays.asList(0.01, 0.5, 1.0, 5.0, 10.0));

        // Act
        List<Double> result = coinResource.calculateCoins(request);

        // Assert
        assertEquals(Arrays.asList(0.01, 0.01, 0.01, 1.0, 1.0, 5.0), result);
    }

    @Test
    void testCalculateCoins_SuccessExactMatch() {
        // Arrange
        CoinRequest request = new CoinRequest();
        request.setTargetAmount(10.0);
        request.setDenominations(Arrays.asList(1.0, 2.0, 5.0, 10.0));

        // Act
        List<Double> result = coinResource.calculateCoins(request);

        // Assert
        assertEquals(Collections.singletonList(10.0), result);
    }

    @Test
    void testCalculateCoins_InvalidDenomination() {
        // Arrange
        CoinRequest request = new CoinRequest();
        request.setTargetAmount(10.0);
        request.setDenominations(Arrays.asList(1.0, 2.0, 3.0)); // 3.0 is invalid

        // Act & Assert
        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
            coinResource.calculateCoins(request);
        });
        assertEquals(400, exception.getResponse().getStatus());
        assertTrue(exception.getMessage().contains("Invalid coin denomination: 3.0"));
    }

    @Test
    void testCalculateCoins_ImpossibleToMakeTarget() {
        // Arrange
        CoinRequest request = new CoinRequest();
        request.setTargetAmount(7.03);
        request.setDenominations(Arrays.asList(1.0, 2.0, 5.0)); // Can't make 0.03

        // Act & Assert
        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
            coinResource.calculateCoins(request);
        });
        assertEquals(400, exception.getResponse().getStatus());
        assertTrue(exception.getMessage().contains("Unable to make up the target amount with the given denominations"));
    }

    @Test
    void testCalculateCoins_EmptyDenominations() {
        // Arrange
        CoinRequest request = new CoinRequest();
        request.setTargetAmount(10.0);
        request.setDenominations(Collections.emptyList());

        // Act & Assert
        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
            coinResource.calculateCoins(request);
        });
        assertEquals(400, exception.getResponse().getStatus());
        assertTrue(exception.getMessage().contains("Unable to make up the target amount with the given denominations"));
    }

    @Test
    void testCalculateCoins_ZeroTargetAmount() {
        // Arrange
        CoinRequest request = new CoinRequest();
        request.setTargetAmount(0.0);
        request.setDenominations(Arrays.asList(0.01, 0.5, 1.0, 5.0, 10.0));

        // Act
        List<Double> result = coinResource.calculateCoins(request);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testCalculateCoins_MultipleSmallCoins() {
        // Arrange
        CoinRequest request = new CoinRequest();
        request.setTargetAmount(0.15);
        request.setDenominations(Arrays.asList(0.01, 0.05, 0.1, 0.5, 1.0));

        // Act
        List<Double> result = coinResource.calculateCoins(request);

        // Assert
        assertEquals(Arrays.asList(0.05, 0.1), result);
    }
}
