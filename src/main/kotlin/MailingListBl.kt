object MailingListBl {
    fun addUser(user: AddUserJson) {
        val newUser = User(user.name)
        UserRepository.add(newUser)
    }

    fun getAllUsers(): List<User> {
        return UserRepository.getAll()
    }
}