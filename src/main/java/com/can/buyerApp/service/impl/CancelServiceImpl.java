@Service
public class CancelServiceImpl implements CancelService {

    @Override
    public ResponseEntity<?> sendCancelRequest(
            String domain,
            String type,
            String transactionId,
            Long cancellationReasonId,
            String orderId,
            String description
    ) {
        try {
            CancelRequest cancelRequest =
                    createCancelRequest(domain, transactionId, cancellationReasonId, orderId, description);

            ResponseEntity<?> response = ondcWebClient.sendCancel(cancelRequest);
            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            log.error("Error in processing Cancel request", e);
            throw new RuntimeException("Error in processing Cancel request", e);
        }
    }
}
