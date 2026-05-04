import exception.InvalidPolicyDataException;
import io.FileHandler;
import model.Insurance;
import java.io.IOException;
import java.util.List;

void main() {
    FileHandler fileHandler = new FileHandler();
    try {
        List<Insurance> policies = fileHandler.loadFile("policies.csv");
        System.out.println("✅ Loaded " + policies.size() + " policies correctly.");

        System.out.println("--- First 5 policies ---");
        policies.stream().limit(5).forEach(System.out::println);

    } catch (IOException e) {
        System.err.println("❌ Error reading the file: " + e.getMessage());
    } catch (InvalidPolicyDataException e) {
        System.err.println("⚠️ Data format error: " + e.getMessage());
    }
}
