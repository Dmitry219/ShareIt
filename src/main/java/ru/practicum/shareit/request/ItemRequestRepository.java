package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
//    @Query(value = "SELECT * FROM REQUESTS r WHERE r.REQUESTOR_ID = ?1 ; " , nativeQuery = true)
//    List<ItemRequest> getItemRequestsByRequesterId(long requestorId);

    List<ItemRequest> findAllByRequestorId(long requestorId);

//    @Query(value = "SELECT * FROM REQUESTS r WHERE r.REQUESTOR_ID != ?1 ORDER BY CREATED DESC ; ", nativeQuery = true)
//    List<ItemRequest> findAllByRequestorIdIsNotOrderByCreatedDesc(long requestorId, PageRequest pageRequest);

    List<ItemRequest> findAllByRequestorIdIsNotOrderByCreatedDesc(long requestorId, PageRequest pageRequest);

}
