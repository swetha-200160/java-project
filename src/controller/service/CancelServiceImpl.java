@Service
@Profile("!local")
public class CancelServiceImpl implements CancelService {

    private final ContextRepository contextRepository;

    public CancelServiceImpl(ContextRepository contextRepository) {
        this.contextRepository = contextRepository;
    }
}
