package net.waret.demo.photoz.repository;

import net.waret.demo.photoz.domain.Order;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
