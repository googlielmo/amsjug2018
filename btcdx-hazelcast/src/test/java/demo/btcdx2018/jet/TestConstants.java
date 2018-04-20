package demo.btcdx2018.jet;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.BiPredicate;

public class TestConstants {

	public static final LocalDate AMS_JUG_EVENT = LocalDate.of(2018, 4, 19);

	public static final BigDecimal ONES = new BigDecimal("1.11");
	public static final BigDecimal TWOS = new BigDecimal("2.22");
	public static final BigDecimal THREES = new BigDecimal("3.33");
	public static final BigDecimal FOURS = new BigDecimal("4.44");
	public static final BigDecimal FIVES = new BigDecimal("5.55");
	public static final BigDecimal SIXES = new BigDecimal("6.66");
	public static final BigDecimal SEVENS = new BigDecimal("7.77");
	public static final BigDecimal EIGHTS = new BigDecimal("8.88");
	public static final BigDecimal NINES = new BigDecimal("9.99");
	// BigDecimal.ZERO is "0" not "0.00"
	public static final BigDecimal ZEROES = new BigDecimal("0.00");
	
	// Check expected item is the last in a list
	public static final BiPredicate<List<?>, List<?>> LAST_ITEM_MATCHES =
			(expected, actual) -> {
				if (actual == null || expected == null || actual.size() == 0 || expected.size() == 0) {
					return false;
				}
				return actual.get(actual.size() - 1).equals(expected.get(expected.size() - 1));
			};
}
