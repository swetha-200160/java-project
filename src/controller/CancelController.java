@RestController
@Profile("!local")
public class CancelController {

    private final CancelService cancelService;

    public CancelController(CancelService cancelService) {
        this.cancelService = cancelService;
    }
}

