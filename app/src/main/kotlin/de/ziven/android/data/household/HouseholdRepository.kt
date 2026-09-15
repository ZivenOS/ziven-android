package de.ziven.android.data.household

import de.ziven.android.data.auth.SecureTokenStore
import de.ziven.shared.api.ApiClient
import de.ziven.shared.model.CreateHouseholdRequest
import de.ziven.shared.model.Household
import de.ziven.shared.model.HouseholdPatch
import de.ziven.shared.model.Invitation
import de.ziven.shared.model.InvitationRequest
import de.ziven.shared.model.JoinHouseholdRequest
import de.ziven.shared.model.ProfilePatch
import de.ziven.shared.model.WeightEntry
import de.ziven.shared.model.WeightLogRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HouseholdRepository @Inject constructor(
    private val client: ApiClient,
    private val store: SecureTokenStore,
) {
    private suspend fun token(): String = store.accessToken ?: error("Not logged in")

    suspend fun current(): Household = client.getCurrentHousehold(token())

    suspend fun create(name: String): Household = client.createHousehold(token(), CreateHouseholdRequest(name))

    suspend fun join(inviteToken: String): Household = client.joinHousehold(token(), JoinHouseholdRequest(inviteToken))

    suspend fun update(patch: HouseholdPatch): Household = client.updateHousehold(token(), patch)

    suspend fun invitations(): List<Invitation> = client.listInvitations(token())

    suspend fun invite(email: String, role: String = "member"): Invitation =
        client.createInvitation(token(), InvitationRequest(email, role))

    suspend fun updateProfile(memberId: String, patch: ProfilePatch) =
        client.updateMemberProfile(token(), memberId, patch)

    suspend fun logWeight(memberId: String, kg: Double, loggedOn: String? = null): WeightEntry =
        client.logWeight(token(), memberId, WeightLogRequest(kg, loggedOn))

    suspend fun weightSeries(memberId: String, weeks: Int = 8): List<WeightEntry> =
        client.getWeightSeries(token(), memberId, weeks)

    suspend fun removeMember(memberId: String) = client.removeMember(token(), memberId)

    suspend fun leave() = client.leaveHousehold(token())
}
