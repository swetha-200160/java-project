@Service
@ConditionalOnProperty(
    name = "feature.cancel.enabled",
    havingValue = "true",
    matchIfMissing = false
)
public class CancelServiceImpl implements CancelService {
    ...
}

