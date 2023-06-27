package com.example.starter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.naming.ldap.PagedResultsControl;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Fee {
  private static final double BANK_TO_BANK = 0.1;
  private static final double BANK_TO_EWALLT = 0;
  private static final double EWALLET_TO_BANK = 0.05;
}
