package br.org.cesar.wificonnect.domain.usecase

interface UseCaseListener {
    /**
     * This function is invoked when the use case starts.
     */
    fun onUseCaseStarted()

    /**
     * This function is invoked by the use case to send a message to listener.
     */
    fun onUseCaseMsgReceived(msg: String)

    /**
     * This function is invoked when the use case finished successfully.
     */
    fun onUseCaseSuccess()

    /**
     * This function is invoked when the use case failed (use case is done).
     */
    fun onUseCaseFailed(reason: String?)
}