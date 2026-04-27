import io.FileHandler;
import model.Insurance;
import java.io.IOException;
import java.util.List;

void main() {
    FileHandler fileHandler = new FileHandler();
    try {
        List<Insurance> policies = fileHandler.loadFile("policies.csv");
        System.out.println("Loaded " + policies.size() + " policies.");
        policies.stream().limit(5).forEach(System.out::println);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
