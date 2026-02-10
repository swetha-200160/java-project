@Service
@Profile("local")
public class CancelServiceLocalImpl implements CancelService {

    @Override
    public void cancel() {
        // Local mode: no DB
    }
}
