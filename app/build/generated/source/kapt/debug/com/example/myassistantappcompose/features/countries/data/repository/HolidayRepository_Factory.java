// Generated by Dagger (https://dagger.dev).
package com.example.myassistantappcompose.features.countries.data.repository;

import com.example.myassistantappcompose.core.data.local.AppDatabase;
import com.example.myassistantappcompose.core.data.remote.HolidayApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import javax.inject.Provider;

@DaggerGenerated
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class HolidayRepository_Factory implements Factory<HolidayRepository> {
  private final Provider<HolidayApi> holidayApiProvider;

  private final Provider<AppDatabase> dbProvider;

  public HolidayRepository_Factory(Provider<HolidayApi> holidayApiProvider,
      Provider<AppDatabase> dbProvider) {
    this.holidayApiProvider = holidayApiProvider;
    this.dbProvider = dbProvider;
  }

  @Override
  public HolidayRepository get() {
    return newInstance(holidayApiProvider.get(), dbProvider.get());
  }

  public static HolidayRepository_Factory create(Provider<HolidayApi> holidayApiProvider,
      Provider<AppDatabase> dbProvider) {
    return new HolidayRepository_Factory(holidayApiProvider, dbProvider);
  }

  public static HolidayRepository newInstance(HolidayApi holidayApi, AppDatabase db) {
    return new HolidayRepository(holidayApi, db);
  }
}
