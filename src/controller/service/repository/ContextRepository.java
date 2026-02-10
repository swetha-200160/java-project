@Repository
@Profile("!local")
public interface ContextRepository
        extends JpaRepository<ContextEntity, Long> {
}
