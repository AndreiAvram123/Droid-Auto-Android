package com.andrei.car_rental_android.screens.Home

import android.location.Location
import app.cash.turbine.test
import com.andrei.car_rental_android.BaseViewModelTest
import com.andrei.car_rental_android.engine.repositories.*
import com.andrei.car_rental_android.helpers.LocationHelper
import com.andrei.car_rental_android.screens.Home.states.NearbyCarsState
import com.andrei.car_rental_android.screens.Home.useCases.CancelReservationUseCase
import com.andrei.car_rental_android.screens.Home.useCases.MakeReservationUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest : BaseViewModelTest() {

 private val carRepository: CarRepository = mockk(relaxed = true)
 private val reservationRepository: ReservationRepository = mockk(relaxed = true)
 private val rideRepository: RideRepository = mockk(relaxed = true)
 private val directionsRepository: DirectionsRepository = mockk(relaxed = true)
 private val makeReservationUseCase: MakeReservationUseCase = MakeReservationUseCase(
  reservationRepository = reservationRepository
 )
 private val cancelReservationUseCase: CancelReservationUseCase = CancelReservationUseCase(
  reservationRepository = reservationRepository
 )
 private val paymentRepository: PaymentRepository = mockk(relaxed = true)
 private val locationHelper: LocationHelper = mockk(relaxed = true)

 private lateinit var sut: HomeViewModel

 @BeforeEach
 fun setup() {
  sut = HomeViewModelImpl(
   coroutineProvider = testScope,
   carRepository = carRepository,
   paymentRepository = paymentRepository,
   directionsRepository = directionsRepository,
   cancelReservationUseCase = cancelReservationUseCase,
   makeReservationUseCase = makeReservationUseCase,
   locationHelper = locationHelper,
   reservationRepository = reservationRepository,
   rideRepository = rideRepository
  )
 }

 @Test
 fun `When the viewModel is created the nearby cars state is Default`() = runDroidAutoTest {
  sut.nearbyCarsState.test {
   assert(awaitItem() is NearbyCarsState.Default)
  }
 }

 @Test
 fun `When not all location requirements are met, the location state will remain Not requested`() =
  runDroidAutoTest {
   sut.notifyRequirementResolved(HomeViewModel.LocationRequirement.PermissionNeeded)
   sut.locationState.test {
    assert(awaitItem() is HomeViewModel.LocationState.NotRequested)
    expectNoEvents()
   }
  }

 @Test
 fun `When all location requirements are met, the location is requested and the location state is Loading`() =
  runDroidAutoTest {
   val determinedLocation = mockk<Location>()
   coEvery {
    locationHelper.getLastKnownLocation()
   } returns determinedLocation

   sut.notifyRequirementResolved(HomeViewModel.LocationRequirement.LocationActive)
   sut.notifyRequirementResolved(HomeViewModel.LocationRequirement.PermissionNeeded)

   sut.locationState.test {
    assert(awaitItem() is HomeViewModel.LocationState.NotRequested)
    assert(awaitItem() is HomeViewModel.LocationState.Loading)
    awaitItem()
   }
  }

 @Test
 fun `When all location requirements are met, and the location was determined, the location state is Resolved `() =
  runDroidAutoTest {
   val determinedLocation = mockk<Location>()
   coEvery {
    locationHelper.getLastKnownLocation()
   } returns determinedLocation

   sut.notifyRequirementResolved(HomeViewModel.LocationRequirement.LocationActive)
   sut.notifyRequirementResolved(HomeViewModel.LocationRequirement.PermissionNeeded)

   sut.locationState.test {
    assert(awaitItem() is HomeViewModel.LocationState.NotRequested)
    assert(awaitItem() is HomeViewModel.LocationState.Loading)
    assert(awaitItem() is HomeViewModel.LocationState.Resolved)
   }
  }


 @Test
 fun `When all location requirements are met, but the location is null, the location state is Unknown `() =
  runDroidAutoTest {
   coEvery {
    locationHelper.getLastKnownLocation()
   } returns null

   sut.notifyRequirementResolved(HomeViewModel.LocationRequirement.LocationActive)
   sut.notifyRequirementResolved(HomeViewModel.LocationRequirement.PermissionNeeded)

   sut.locationState.test {
    assert(awaitItem() is HomeViewModel.LocationState.NotRequested)
    assert(awaitItem() is HomeViewModel.LocationState.Loading)
    assert(awaitItem() is HomeViewModel.LocationState.Unknown)
   }
  }

 @Test
 fun `When the location is not determined but after retry is determined, the location state is Resolved`() =
  runDroidAutoTest {
   sut.notifyRequirementResolved(HomeViewModel.LocationRequirement.LocationActive)
   sut.notifyRequirementResolved(HomeViewModel.LocationRequirement.PermissionNeeded)

   coEvery {
    locationHelper.getLastKnownLocation()
   } returns null

   sut.locationState.test {
    assert(awaitItem() is HomeViewModel.LocationState.NotRequested)
    assert(awaitItem() is HomeViewModel.LocationState.Loading)
    assert(awaitItem() is HomeViewModel.LocationState.Unknown)
   }
   val determinedLocation = mockk<Location>()
   coEvery {
    locationHelper.getLastKnownLocation()
   } returns determinedLocation

   sut.locationState.test {
    sut.retryGetLocation()
    assert(awaitItem() is HomeViewModel.LocationState.Unknown)
    assert(awaitItem() is HomeViewModel.LocationState.Loading)
    assert(awaitItem() is HomeViewModel.LocationState.Resolved)
   }

  }


 @Test
 fun `When the user location is not determined, the nearby cars state will be Error with unknown location message`() =
  runDroidAutoTest {

   sut.notifyRequirementResolved(HomeViewModel.LocationRequirement.LocationActive)
   sut.notifyRequirementResolved(HomeViewModel.LocationRequirement.PermissionNeeded)

   coEvery {
    locationHelper.getLastKnownLocation()
   } returns null

   sut.nearbyCarsState.test {
    assert(awaitItem() is NearbyCarsState.Loading)
    assert(awaitItem() is NearbyCarsState.ErrorUnknownLocation)
   }
  }


 @Test
 fun `When the user location is determined and the API returns success , the nearby cars state is Success`() =
  runDroidAutoTest {


   sut.notifyRequirementResolved(HomeViewModel.LocationRequirement.LocationActive)
   sut.notifyRequirementResolved(HomeViewModel.LocationRequirement.PermissionNeeded)

   val determinedLocation = mockk<Location>()

   coEvery {
    locationHelper.getLastKnownLocation()
   } returns determinedLocation

   returnSuccess {
    carRepository.getNearbyCars(determinedLocation)
   }
   sut.nearbyCarsState.test {
    assert(awaitItem() is NearbyCarsState.Loading)
    assert(awaitItem() is NearbyCarsState.Success)
   }
  }


 @Test
 fun `When the user location is determined and the API returns Error , the nearby cars state is Error`() = runDroidAutoTest {
   sut.notifyRequirementResolved(HomeViewModel.LocationRequirement.LocationActive)
   sut.notifyRequirementResolved(HomeViewModel.LocationRequirement.PermissionNeeded)
   val determinedLocation = mockk<Location>()
   coEvery {
    locationHelper.getLastKnownLocation()
   } returns determinedLocation

   returnError(500) {
    carRepository.getNearbyCars(determinedLocation)
   }
   sut.nearbyCarsState.test {
    assert(awaitItem() is NearbyCarsState.Loading)
    assert(awaitItem() is NearbyCarsState.Error)
   }
  }


 @Test
 fun `When the location is determined the camera position will contain the location`() =
  runDroidAutoTest {
   sut.notifyRequirementResolved(HomeViewModel.LocationRequirement.LocationActive)
   sut.notifyRequirementResolved(HomeViewModel.LocationRequirement.PermissionNeeded)
   val location = mockk<Location>()
   coEvery {
    locationHelper.getLastKnownLocation()
   } returns location

   sut.cameraPosition.test {
    assert(awaitItem() == null)
    assert(awaitItem() != null)
   }
  }
}
