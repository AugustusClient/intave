package de.jpx3.intave.connect.sibyl.auth;

import de.jpx3.intave.tools.annotate.KeepEnumInternalNames;

@KeepEnumInternalNames
public enum SibylAuthenticationState {
  N, //none
  AW_AK, // awaiting authkey
  AW_AKV, // awaiting authkey
  ATH, // authenticated
  RGF // rejected
}
